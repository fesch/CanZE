<?php
	$files = `find * | grep \/Icon`;
	$lines = explode("\n",$files);
	foreach($lines as $line)
	{
		echo "Deleting: $line\n";
		`rm $line`;
	}
?>