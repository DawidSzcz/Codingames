<?php

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

?>