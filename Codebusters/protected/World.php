<?php

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

?>