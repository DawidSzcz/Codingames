<?php

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

?>