#!/bin/bash
# ec2-start-servers <server1 server2 ...>
. scripts/planetlab/pl-common.sh

if [ -z "$*" ]; then
        echo "ec2-start-servers.sh <ec2-instance1 ec2-instance2 ...>"
        echo "Starts multi-dc Swift store configuration on provided ec2 hosts."
	exit 1
fi

# start <server1 server2 ...>
start() {
	servers=$*
	echo "Starting swift servers: $servers"
	# TODO: logging!
	i=0
	for server in $servers; do
		other_servers=${servers//$server/}
		swift_app_cmd swift.dc.DCSequencerServer -name "X$i" -sequencers $other_servers -servers localhost
		run_cmd_bg $server $CMD
		sleep 2
		swift_app_cmd swift.dc.DCServer -sequencer localhost
		run_cmd_bg $server $CMD
		i=$(($i+1))
	done
}

start $*
