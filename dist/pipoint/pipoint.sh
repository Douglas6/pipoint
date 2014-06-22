# Adjust the EXEC file location variable as needed
EXEC="/home/pi/pipoint/pipoint.jar"

# For running in a window on the desktop
java -jar $EXEC
# For running fullscreen with a touchscreen; no window borders and hide the mouse
#java -jar $EXEC -f
# For saving logs to a file; adjust the configuration file name and add the -f flag as needed
# Logs will be written to /tmp/pipoint.log on a *nix system
#java -Djava.util.logging.config.file=/home/pi/pipoint/logging.properties -jar $EXEC