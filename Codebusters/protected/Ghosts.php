<?php

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

?>