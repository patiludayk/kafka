#!/bin/bash
#script to start zookeeper and kafka server

function checkServerStarted() {
  waitFor=0
  serverStarted=-1
  while [[ "$waitFor" -lt 15 ]]; do
    nc -z -v -G5 $2 $3 &>/dev/null
    serverStarted=$?
    if [  "$serverStarted" == 0 ]; then
      echo  "$1 started."
      break
    else
      echo -n "* "
    fi
    sleep 1
    ((waitFor++))
  done
  if [ $serverStarted != 0 ]; then
      printf "\n%s\n" "error starting $1, please check port $3 binding or $1 logs for more detail."
  fi
}

current_date_time="`date "+%Y-%m-%d %H:%M:%S"`";
source src/main/resources/scripts/input.txt
##########################ZOOKEEPER##########################
#zookeeperStart="/Users/udaypatil/kafka_2.13-3.2.1/bin/zookeeper-server-start.sh"
#zookeeperStop="/Users/udaypatil/kafka_2.13-3.2.1/bin/zookeeper-server-stop.sh"
#zookeeperProperties="/Users/udaypatil/kafka_2.13-3.2.1/config/zookeeper.properties"

# Set the zookeeper log filename
#zookeeperLogs="/Users/udaypatil/IdeaProjects/kafka-basics/src/main/resources/logs/zk.out"
# Check the file is exists or not
if [ -f $zookeeperLogs ]; then
    rm $zookeeperLogs
    echo ">>>  old $zookeeperLogs file removed."
  else
    echo "no old file available."
fi
echo "************************************************************************************************************"
printf "%s\n%s\n" "zookeeper start: $zookeeperStart" "zookeeperProperties: $zookeeperProperties"
echo "************************************************************************************************************"
echo ">>>>>>>>>  1.Starting zookeeper  <<<<<<<<<"
sh -c "$zookeeperStart $zookeeperProperties" >> $zookeeperLogs 2>&1 &
#echo "zookeeper logs1" 2>&1 > $zookeeperLogs &
checkServerStarted "zookeeper" $hostName $zookeeperPort
########zookeeper end############

#read -p "press enter to continue..."
echo

##########################KAFKA##########################
#kafkaStart=/Users/udaypatil/kafka_2.13-3.2.1/bin/kafka-server-start.sh
#kafkaStop=/Users/udaypatil//kafka_2.13-3.2.1/bin/kafka-server-stop.sh
#kafkaProperties=/Users/udaypatil/kafka_2.13-3.2.1/config/server.properties

# Set the kafka log filename
#kafkaLogs='/Users/udaypatil/IdeaProjects/kafka-basics/src/main/resources/logs/kafka.out'
# Check the file is exists or not
if [ -f $kafkaLogs ]; then
  rm $kafkaLogs
  echo ">>>  old $kafkaLogs file removed."
fi
echo "************************************************************************************************************"
printf "%s\n%s\n" "kafka start: $kafkaStart" "kafkaProperties: $kafkaProperties"
echo "************************************************************************************************************"
echo ">>>>>>>>>  2.Starting kafka  <<<<<<<<<"
sh -c "$kafkaStart $kafkaProperties" 2>&1 > "$kafkaLogs" &
#echo "kafka logs" $date 2>&1 >$kafkaLogs &

#check if kafka server started or not
checkServerStarted "kakfa" $hostName $kafkaPort

echo "************ exiting starter script ***************"