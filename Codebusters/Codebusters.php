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


class NPC
{
    public static $states = [
        0   => 'IDLE',
        1   => 'CARRY',
        2   => 'STUNNED',
        3   => 'TRAPPING'

    ];
    public  $id, // buster id or ghost id
        $x,
        $y, // position of this buster / ghost
        $state, // For busters: 0=idle, 1=carrying a ghost.
        $value,
        $visible,
        $stolen = false,
        $radar = true;

    function __construct($t) {
        $this->id = $t['id'];
        $this->update($t);
    }

    function update($t) {
        $this->x = $t['x'];
        $this->y = $t['y']; // position of this buster / ghost
        $this->state = $t['state']; // For busters: 0=idle, 1=carrying a ghost.
        $this->value = $t['value']; // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.
    }

    function toString() {
        error_log("$this->type ($this->id): ($this->x x $this->y) STATE: ".(($this->state)<4 ? NPC::$states[$this->state] : $this->state).
            " VALUE $this->value ".($this->visible ? "T" :"F").($this->radar ? "T" :"F").($this->stolen ? "T" :"F"));
    }

    function aviable(){
        return $this->entityType != -1 && ($this->state == 3 || $this->state == 0) && $this->command == null;
    }

    function dist($p)
    {
        if(gettype($p) != 'array'){
            if(!isset($p->x)) {
                error_log(var_export($p, true));
            }
            $p = [$p->x, $p->y];
        }
        return sqrt(pow($this->x - $p[0], 2) + pow($this->y - $p[1], 2));
    }

}


class Goal
{
    public  $types = ['POINT', 'GHOST', 'ENEMY', 'BASE'],
        $type,
        $goal;
}

fscanf(STDIN, "%d", $bustersPerPlayer);
fscanf(STDIN, "%d", $ghostCount);
fscanf(STDIN, "%d", $myTeamId);

$granul = 500;
$range = 1760;
$visibility = 2200;
$route_length = 20;
$min_range = 900;
$frp = $myTeamId ? [12000, 4000] : [4000, 5000];
$world = new World($myTeamId, $granul, $min_range, $range, $visibility, $route_length,  $frp);


while (TRUE)
{
    $world->update();
    $world->toString();
    $world->makeMove();
}


class World
{
    public  $enemies = [],
        $allies,
        $gohsts = [],
        $e_num,
        $myTeamId;

    function __construct($myTeamId, $granul, $min_range, $range, $visibility, $route_length, $frp)
    {
        $this->myTeamId = $myTeamId;
        $this->allies   = new Allies($myTeamId, $granul, $min_range, $range, $visibility, $route_length, $frp);
        $this->ghosts   = new Ghosts($min_range, $range, $visibility);
        $this->enemies  = new Enemies($min_range, $range);
    }

    function update()
    {
        $this->ghosts->resetVisibility();
        $this->enemies->resetVisibility();
        fscanf(STDIN, "%d", $this->e_num);
        for ($i = 0; $i < $this->e_num; $i++)
        {
            $t = [];
            fscanf(STDIN, "%d %d %d %d %d %d",
                $t['id'], // buster id or ghost id
                $t['x'],
                $t['y'], // position of this buster / ghost
                $t['entityType'], // the team id if it is a buster, -1 if it is a ghost.
                $t['state'], // For busters: 0=idle, 1=carrying a ghost.
                $t['value'] // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.
            );

            switch($t['entityType'])
            {
                case -1:
                    $this->ghosts->update($t);
                    break;
                case $this->myTeamId:
                    $this->allies->update($t);
                    break;
                case 1 - $this->myTeamId:
                    $this->enemies->update($t);
            }
        }
        $this->ghosts->removeUpsend($this->allies);
        $this->allies->updateBoard();
    }

    function makeMove()
    {
        $this->allies->makeMove($this->enemies, $this->ghosts);
    }

    function notStunnedEnemies()
    {
        return array_filter($this->enemies, function($n) { return $n->state !== 2;});
    }

    function toString()
    {
        $this->allies->toString();
        $this->enemies->toString();
        $this->ghosts->toString();

        error_log('******************************');
    }
}


class NPCs
{
    public  $a = [],
        $min_range,
        $range;

    function __construct($min_range, $range)
    {
        $this->min_range = $min_range;
        $this->range = $range;
    }

    function update($t_npc)
    {
        if(!isset($this->a[$t_npc['id']])){
            $this->a[$t_npc['id']] = new $this->class($t_npc);
        } else {
            $this->a[$t_npc['id']]->update($t_npc);
        }
    }

    function toString()
    {
        foreach($this->a as $npc){
            $npc->toString();
        }
    }

    function resetVisibility(){
        foreach($this->a as $npc){
            $npc->visible = false;
        }
    }

    function num()
    {
        return count($this->a);
    }
}


class Enemy extends NPC
{
    public $type = 'ENEMY';


    function __construct($t)
    {
        parent::__construct($t);
        $this->visible = true;
    }

    function update($t)
    {
        parent::update($t);
        $this->visible = true;
    }

    function visible()
    {
        return $this->visible;
    }
}


class Enemies extends NPCs
{
    public $class = 'Enemy';

    function visibleEnemies()
    {
        return array_filter($this->a, function($n) { return $n->visible();});
    }
    function carringAccesible($ally)
    {
        $a = array_filter($this->a, function($n) use($ally){return ($ally->dist($n) < $this->range && $n->state==3);});
        return !empty($a);
    }
}


class Ghosts extends NPCs
{
    public  $class = 'Ghost',
        $visible_flex = 500;

    function __construct($min_range, $range, $visibility)
    {
        $this->visibility = $visibility;
        parent::__construct($min_range, $range);
    }

    function accesible($ally)
    {
        return array_filter($this->a, function($n) use($ally){
            return $n->visible && ($ally->dist($n) > $this->min_range && $ally->dist($n) < $this->range);
        });
    }

    function busted($ghost)
    {
        unset($this->a[$ghost->id]);
    }

    function removeUpsend($allies)
    {
        foreach($this->a as $gohst){
            foreach($allies->a as $ally){
                if($ally->dist($gohst) < ($this->visibility - $this->visible_flex) && !$gohst->visible){
                    unset($this->a[$gohst->id]);
                }
            }
        }
    }
}


class Ally extends NPC
{
    public  $stun,
        $command,
        $type = 'ALLY',
        $chaseTime;

    function __construct($t)
    {
        parent::__construct($t);
        $this->command = null;
        $this->stun = 0;
        $this->goal = null;
        $this->chaseTime = 0;
    }

    function startChasing($chaseTime)
    {
        $this->chaseTime = $chaseTime;
    }

    function chase($enemyBase, $range, &$enemies)
    {
        $e =  $enemies->carringAccesible($this);
        if(!empty($e)) {
            $this->stunEnemy($e, $range, 'CH');
            $this->chaseTime = 0;
        }
        if($this->chaseTime > 0) {
            $this->moveToPoint($enemyBase, 'CH');
        }
    }

    function update($t)
    {
        $this->command = null;
        $this->stolen = false;
        $this->stun = $this->stun-1;
        $this->chaseTime = $this->chaseTime -1;
        if($this->state == 1 && $t['state'] ==2){
            $this->stolen = true;
        }
        parent::update($t);
    }

    function moveToPoint($point, $origin)
    {
        $this->command("MOVE {$point[0]} {$point[1]}", $origin);
    }

    function firstRadar($moves, $point)
    {
        if($moves == 0) {
            $this->command("RADAR", "FR");
            $this->radar = false;
        }
        else {
            $this->moveToPoint($point, "FR");
        }
    }

    function command($command, $origin)
    {
        if($command == "RADAR"){
            $this->radar = false;
        }
        $this->command = $command.' '.$this->id.' '.$origin;
    }

    function moveToGhost($ghost, $min_range, $range, $ghosts, $origin)
    {
        if($ghost == null) {
            return;
        }

        $dist = $this->dist($ghost);
        if($dist < $range && $dist > $min_range){
            $this->command("BUST $ghost->id", $origin);
            return;
        }

        $dx = abs($this->x - $ghost->x);
        $dy = abs($this->y - $ghost->y);
        if($dx == 0){
            $this->moveToPoint([$ghost->x,($ghost->y + ($dy > 0 ? 1000 : -1000))], $origin);
            return;
        }

        $cos = $dx/$dist;
        $a = $dy / $dx;
        $b = $ghost->y - $a * $ghost->x;

        if($this->x < $ghost->x){
            $x = floor($ghost->x - 1000*$cos);
        } else {
            $x = floor($ghost->x + 1000*$cos);
        }
        $y = floor($a*$x + $b);
        $this->moveToPoint([$x, $y], $origin);
    }

    function stunEnemy(&$enemies, $range, $origin = 'STUN')
    {
        foreach($enemies as $enemy){
            if($enemy->state == 3 && $this->dist($enemy) < $range){
                $this->command("STUN $enemy->id", $origin);
                $this->stun = 20;
                $enemy->state = 2;
                return true;
            }
        }
        foreach($enemies as $enemy){
            if($enemy->state != 2 && $this->dist($enemy) < $range){
                $this->command("STUN $enemy->id", 'STUN');
                $this->stun = 20;
                $enemy->state = 2;
                return true;
            }
        }
        return false;
    }

    function notCarring()
    {
        return ($this->state != 2) && ($this->state != 1) && $this->command == null;
    }

    function aviable()
    {
        return ($this->state == 0) && $this->command == null;
    }
}


class Board
{
    # >= 0 rounds till visible
    # -1 at ally route
    # >=1000 not visible yet
    # -2 in enemy area
    public  $b,
        $granul,
        $route_length,
        $x_num,
        $y_num,
        $visibility,
        $y=9000,
        $x=16000;

    function __construct($granul, $visibility, $route_length)
    {
        $this->granul = $granul;
        $this->x_num = $this->x/$granul;
        $this->y_num = $this->y/$granul;
        $this->route_length = $route_length;
        $this->visibility = $visibility;

        error_log("create BOARD [{$this->x_num}] [{$this->y_num}]");
        $this->traverse(0, $this->x_num, 0, $this->y_num, function() {return 1000;});

        $this->traverse(0, ceil($this->x_num/3), 0, ceil($this->y_num/3), function($x) {return -($this->route_length+1);});
        $this->traverse( floor(2*$this->x_num/3), $this->x_num, floor(2*$this->y_num/3), $this->y_num, function($x) {return -($this->route_length+1);});
    }

    function traverse($min_x, $max_x, $min_y, $max_y, $foo)
    {
        for($y = $min_y; $y < $max_y; $y++) {
            for($x = $min_x; $x < $max_x; $x++) {
                $this->b[$y][$x] = (isset($this->b[$y][$x]) ? $foo($this->b[$y][$x]) : $foo());
            }
        }
    }

    function update($allies)
    {
        //add 1
        $this->traverse(0, $this->x_num, 0, $this->y_num, function($x) {return ($x >= -$this->route_length) ? $x +1 : $x;});

        foreach($this->b as $y => $row){
            foreach($row as $x => $point){
                $this->setVisibility($x, $y, $allies);
            }
        }
    }
    # Jak przejede po polu wroga to trudno
    function setVisibility($x, $y, $allies)
    {
        foreach($allies as $ally){
            foreach($this->getVertices($x, $y) as $point){
                if($ally->dist($point) > $this->visibility){
                    continue 2;
                }
            }
            $this->b[$y][$x] = 0;
        }
    }

    function getVertices($x, $y)
    {
        $mult = $this->granul;
        return [[$x*$mult, $y*$mult],
            [($x+1)*$mult, $y*$mult],
            [($x+1)*$mult, ($y+1)*$mult],
            [$x*$mult, ($y+1)*$mult]];
    }

    # . - visible
    # # - enemy
    # ~ - on route
    # % - not seen
    # o seen
    function toString()
    {
        foreach($this->b as $y => $row){
            $s = '';
            foreach($row as $x => $point){
                if($point === 0){
                    $s = $s.'.';
                }
                if($point < -$this->route_length){
                    $s = $s.'#';
                }
                if($point >= 100 && $point < 1000){
                    $s = $s.'~';
                }
                if($point > 0 && $point < 100){
                    $s = $s.'o';
                }
                if($point >= 1000){
                    $s = $s.'%';
                }
            }
            error_log($s);
        }
        error_log('********************************************');
    }

    function getGoal($ally)
    {
        $max = 0;
        foreach($this->b as $y => $row){
            foreach($row as $x => $v){
                if($v > $max){
                    $max = $v;
                }
            }
        }

        $oldest = [];
        foreach($this->b as $y => $row){
            foreach($row as $x => $v){
                if($v == $max){
                    array_push($oldest, [$x*$this->granul, $y*$this->granul]);
                }
            }
        }
        $goal = $oldest[array_rand($oldest)];
        #$this->setRoute($goal, $ally);
        return $goal;
    }

    function setRoute($goal, $ally)
    {
        $a = ($goal[1] - $ally->y)/($goal[0] - $ally->x) ;
        $b = $goal[1] - $a * $goal[0];

        foreach($this->b as $y => $row){
            foreach($row as $x => $v){
                $v = true;
                foreach($this->getVertices($x, $y) as $point){
                    if( $this->b[$y][$x] < $this->route_length                ||
                        $this->dist_line($a, $b, $point) > $this->visibility  ||
                        !$this->between($point, [$ally->x, $ally->y], $goal)) {
                        continue 2;
                    }
                }
                $this->b[$y][$x] = $this->b[$y][$x]-$this->route_length;
            }
        }
    }

    function between($p, $f, $s)
    {
        return  $p[0] < max([$f[0], $s[0]]) &&
        $p[0] > min([$f[0], $s[0]]) &&
        $p[1] < max([$f[1], $s[1]]) &&
        $p[1] > min([$f[1], $s[1]]);
    }

    function dist_line($a, $b, $p)
    {
        return abs($a*$p[0] + $p[1] + $b) / sqrt($a*$a +1);
    }
}


class Ghost extends NPC
{
    public  $type = 'GHOST',
        $visible;

    function __construct($t)
    {
        parent::__construct($t);
        $this->visible = true;
    }

    function update($t)
    {
        parent::update($t);
        $this->visible = true;
    }

    function timeToTrap()
    {
        return ceil($this->state / $this->value);
    }

    function cost($ally, $move, $base)
    {
        $distCost = ceil($this->dist($ally)/$move);
        $distFBCost = ceil($this->dist($base) /$move);
        return $this->state + 40*($this->visible?0:1) + $distFBCost + $distCost;
    }
}

