<?php
    $time_start = microtime(true); // Засекаем время начала выполнения скрипта
    
    define('WUO_ROOT', dirname(__FILE__));

    include_once("class/config.php");
    include_once("inc/functions.php");
    include_once("config.php");
    
    $route=GetDef("route");
    $action=GetDef("action");
    
    if ($route!=""):	
	$route=trim($route);		
	if (is_file(WUO_ROOT."/controller/server/".$route.".php")):
	    include_once WUO_ROOT."/controller/server/".$route.".php";    
	else:
	    if ($cfg->debug):
		echo WUO_ROOT."/controller/server/".$route.".php\n";
	    endif;
	    die("Указаный файл не существует!");
	endif;	
    endif;
    
    if ($action!=""):
	if (is_file(WUO_ROOT."/controller/client/".$action.".php")):
	    include_once WUO_ROOT."/controller/client/index.php";    
	else:
	    die("Указаный файл не существует!");
	endif;
    endif;
    

?>