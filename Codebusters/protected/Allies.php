<?php

class Allies extends NPCs
{
    public  $goBase,
        $chaseEnemy,
        $board,
        $myTeamId,
        $myBase,
        $theirBase,
        $visibility,
        $class = 'Ally',
        $relaseRange = 1600,
        $move = 800,
        $range,
        $route_length,
        $min_range,
        $firstRadarMoves = 5,
        $radarPoint;

    function __construct($myTeamId, $granul, $min_range, $range, $visibility, $route_length,  $frp)
    {
        parent::__construct($min_range, $range);
        error_log('create ALLIES');

        $this->myBase = [
            16000 * $myTeamId,
            9000 * $myTeamId
        ];
        $this->theirBase = [
            $myTeamId ? 1500 : 14500 ,
            $myTeamId ? 1500 : 7500
        ];

        $this->range = $range;
        $this->radarPoint = $frp;
        $this->min_range = $min_range;
        $this->board = new Board($granul, $visibility, $route_length);
        $this->myTeamId = $myTeamId;
        $this->visibility = $visibility;
        $this->goBase = "MOVE {$this->myBase[0]} {$this->myBase[1]}";
        $this->chaseEnemy = "MOVE {$this->theirBase[0]} {$this->theirBase[1]}";
        $this->route_legth = $route_length;
    }

    function updateBoard()
    {
        $this->board->update($this->a);
    }

    function goHome()
    {
        foreach($this->alliesCarring() as $id => $ally)
        {
            $ally->command($ally->dist($this->myBase) < $this->relaseRange? "RELEASE" : $this->goBase, 'GH');
        }
    }

    function makeMove($enemies, $ghosts)
    {
        $this->anyStolen();
        $this->chase($enemies);
        $this->firstRadar();
        $this->stunEnemy($enemies);
        $this->helpCatch($ghosts);
        $this->goHome();
        $this->catchGhosts($ghosts);
        $this->radar($ghosts);
        $this->explore();
        $this->printCommands();
    }

    function anyStolen()
    {
        foreach($this->alliesStolen() as $stolen){
            foreach($this->alliesNotCarring() as $id => $ally) {
                if($stolen->dist($this->theirBase) -2000< $stolen->dist($this->theirBase)) {
                    $ally->startChasing(ceil($stolen->dist($this->theirBase) / $this->move));
                }
            }
        }
    }

    function chase(&$enemies)
    {
        foreach($this->alliesNotCarring() as $chasing){
            $chasing->chase($this->theirBase, $this->range, $enemies);
        }
    }

    function stunEnemy(&$enemies)
    {
        foreach($this->alliesStunable() as $id => $ally){
            $ve = $enemies->visibleEnemies();
            if($ally->stunEnemy($ve, $this->range)){
                continue;
            }
        }
    }

    function helpCatch($ghosts)
    {
        $ghostBusted = $this->ghostBusted();
        foreach($this->alliesNotCarring() as $id => $ally) {
            $minDistance = 300000;
            $goalGhost = null;
            foreach($ghostBusted as $ghostId)
            {
                $ghost = $ghosts->a[$ghostId];
                $dist = $ally->dist($ghost);
                $time = $ghost->timeToTrap();
                if ($dist < $minDistance &&
                    $time > $dist/$this->move ||
                    $ally->value == $ghostId) {
                    $goalGhost = $ghost;
                    $minDistance = $ally->dist($ghost);
                }
            }
            $ally->moveToGhost($goalGhost, $this->min_range, $this->range, $ghosts, 'HC');
        }

    }

    function FirstRadar(){
        if($this->firstRadarMoves >=0) {
            $this->takeClosest($this->radarPoint)->firstRadar($this->firstRadarMoves, $this->radarPoint);
            $this->firstRadarMoves = $this->firstRadarMoves -1;
        }
    }

    function takeClosest($point)
    {
        $minDist = 300000;
        $goalAlly = null;
        foreach($this->a as $ally){
            if($ally->dist($point) < $minDist) {
                $goalAlly = $ally;
                $minDist = $ally->dist($point);
            }
        }

        return $goalAlly;
    }
    function radar($ghosts)
    {
        if($ghosts->num()<2){
            $mindist=8000;
            $goalAlly = null;
            foreach($this->alliesNotCarring() as $ally) {
                if($ally->radar && $ally->state != 2 && ($ally->dist($this->myBase) > $mindist)){
                    $goalAlly = $ally;
                    $minDist = $ally->dist($this->myBase);
                }
            }
            if($goalAlly != null) {
                $goalAlly->command("RADAR", "R");
            }
        }
    }

    function catchGhosts($ghosts)
    {
        foreach($this->alliesNotCarring() as $id => $ally) {
            $cost = 200;
            $goalGhost = null;
            foreach($ghosts->a as $ghost)
            {
                if($ghost->cost($ally, $this->move, $this->myBase) < $cost){
                    $goalGhost = $ghost;
                    $cost = $ghost->cost($ally, $this->move, $this->myBase);
                }
            }
            if($goalGhost != null){
                $ally->moveToGhost($goalGhost, $this->min_range, $this->range, $ghosts, 'CG');
            }
        }
    }

    function explore()
    {
        foreach($this->alliesAviable() as $id => $ally)
        {
            if(isset($ally->goal)){
                $goal = $ally->goal;
                $ally->command("MOVE {$goal[1]} {$goal[2]}", 'EXG');
                if($goal[0] == 1 || $ally->dist([$goal[1], $goal[2]]) < $this->visibility) {
                    $ally->goal = null;
                } else {
                    $ally->goal  = [$goal[0] - 1, $goal[1], $goal[2]];
                }
            } else {
                $goal = $this->board->getGoal($ally);
                $ally->command("MOVE {$goal[0]} {$goal[1]}", 'EX');
                $ally->goal  = [$this->route_length, $goal[0], $goal[1]];
            }
        }
    }

    function printCommands()
    {
        foreach($this->a as $ally){
            echo (($c = $ally->command) == null ? $this->goBase : $c)."\n";
        }
    }

    function alliesAviable()
    {
        return array_filter($this->a, function($n) { return $n->aviable();});
    }
    function alliesStolen()
    {
        return array_filter($this->a, function($n) { return $n->stolen;});
    }

    function ghostBusted()
    {
        $ghosts = [];
        foreach($this->a as $ally){
            if($ally->state == 3){
                array_push($ghosts, $ally->value);
            }
        }
        return $ghosts;
    }

    function alliesStunable()
    {
        return array_filter($this->a, function($n) { return !isset($n->command) && $n->stun <= 0;});
    }

    function alliesCarring()
    {
        return array_filter($this->a, function($n) { return $n->state == 1;});
    }

    function alliesNotCarring()
    {
        return array_filter($this->a, function($n) { return $n->notCarring();});
    }

    function toString()
    {
        $this->board->toString();
        parent::toString();
    }
}

?>