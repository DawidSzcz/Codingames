<?php

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

?>