<?php

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

?>