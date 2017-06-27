<?php
    $time_start = microtime(true); // Засекаем время начала выполнения скрипта
    
    define('WUO_ROOT', dirname(__FILE__));

    include_once("class/config.php");
    include_once("inc/functions.php");
    include_once("config.php");
    
    $route=GetDef("route");
    $action=GetDef("action");
    
    if ($route!=""):
	if (is_file(WUO_ROOT."/client/server/".$route.".php")):
	    include_once WUO_ROOT."/client/server/".$route.".php";    
	else:
	    die("Указаный файл не существует!");
	endif;	
    endif;
    
    if ($action!=""):
	include_once WUO_ROOT."/client/client/index.php";    
    endif;
    

?>