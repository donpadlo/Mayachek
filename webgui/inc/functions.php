<?php

/**
 * Возвращает значение $_GET[$name] или $def
 * @param string $name
 * @param string $def
 * @return string
 */
function GetDef($name, $def = '') {
	global $PARAMS;
	if (isset($_GET[$name])) {
		return $_GET[$name];
	} else if (isset($PARAMS[$name])) {
		return $PARAMS[$name];
	} else {
		return $def;
	}
}
?>
