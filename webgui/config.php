<?php
    $cfg=new Config();
    $cfg->sql_host="localhost";
    $cfg->sql_base="mayachek";
    $cfg->sql_user="mayachek";
    $cfg->sql_pass="PpPazx";
    $cfg->debug=true;
    $cfg->connectDB();        
    
if ($cfg->debug) {
	ini_set('display_errors', 1);
	error_reporting(E_ALL);
};
?>