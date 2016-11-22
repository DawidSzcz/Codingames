<?php

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

?>