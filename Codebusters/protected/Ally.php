<?php

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

?>