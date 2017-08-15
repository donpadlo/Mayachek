<?php

class Config {
    public $debug=false;
    public $sql_host="localhost";
    public $sql_user="root";
    public $sql_pass="";
    public $sql_base="";        
    public $db;
    function connectDB(){
	try {
	  $this->db=new PDO('mysql:host='.$this->sql_host.';dbname='.$this->sql_base, $this->sql_user, $this->sql_pass);	  
	} catch (PDOException $e) {
	   print "Error!: " . $e->getMessage() . "<br/>";
	   die();
	};
    }
};
