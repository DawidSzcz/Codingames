<?php

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

?>
