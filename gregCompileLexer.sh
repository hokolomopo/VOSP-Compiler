if (($# < 1))
then
	echo -e "Missing argument : flex file to compile - defaulting to flex/VSOP.jflex"
	java -jar "/mnt/c/Program files/jflex-1.7.0/lib/jflex-full-1.7.0.jar" -d src -nobak flex/VSOP.jflex
else
	java -jar "/mnt/c/Program files/jflex-1.7.0/lib/jflex-full-1.7.0.jar" -d src -nobak $1
fi
