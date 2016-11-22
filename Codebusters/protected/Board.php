<?php

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

?>