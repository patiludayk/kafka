#!/bin/bash
#script to stop kafka and zookeeper server

function checkServerStopped() {
  waitFor=0
  serverStopped=-1
  while [[ "$waitFor" -lt 15 ]]; do
    nc -z -v -G5 $2 $3 &>/dev/null
    serverStopped=$?
    if [ "$serverStopped" == 0 ]; then
      printf "%s" "* "
    else
      printf "\n%s\n" "$1 stopped on port $3. Check $1 logs for more detail."
      break
    fi
    sleep 1
    ((waitFor++))
  done
  if [ $serverStopped == 0 ]; then
    printf "%s\n" "error stopping $1 on port $3. Check $1 logs for more detail."
  fi
}

current_date_time="$(date "+%Y-%m-%d %H:%M:%S")"
source src/main/resources/scripts/input.txt

##########################KAFKA##########################
#kafkaStart=/Users/udaypatil/kafka_2.13-3.2.1/bin/kafka-server-start.sh
#kafkaStop=/Users/udaypatil//kafka_2.13-3.2.1/bin/kafka-server-stop.sh
#kafkaProperties=/Users/udaypatil/kafka_2.13-3.2.1/config/server.properties

# Set the kafka log filename
#kafkaLogs='/Users/udaypatil/IdeaProjects/kafka-basics/src/main/resources/logs/kafka.out'
# Check the file is exists or not
if [ ! -f $kafkaLogs ]; then
  echo ">>>  $kafkaLogs file not available."
fi
echo "************************************************************************************************************"
printf "%s\n" "stopping kafka: $kafkaStop"
echo "************************************************************************************************************"
echo ">>>>>>>>>  1.Stopping kafka  <<<<<<<<<"
sh -c "$kafkaStop" 2>&1 >> "$kafkaLogs" &
#echo "kafka logs" $date 2>&1 >> $kafkaLogs &

#check if kafka server started or not
checkServerStopped "kakfa" $hostName $kafkaPort

##########################ZOOKEEPER##########################
#zookeeperStart="/Users/udaypatil/kafka_2.13-3.2.1/bin/zookeeper-server-start.sh"
#zookeeperStop="/Users/udaypatil/kafka_2.13-3.2.1/bin/zookeeper-server-stop.sh"
#zookeeperProperties="/Users/udaypatil/kafka_2.13-3.2.1/config/zookeeper.properties"

# Set the zookeeper log filename
#zookeeperLogs="/Users/udaypatil/IdeaProjects/kafka-basics/src/main/resources/logs/zk.out"
# Check the file is exists or not
if [ ! -f $zookeeperLogs ]; then
  echo "$zookeeperLogs file not available."
fi
echo "************************************************************************************************************"
printf "%s\n" "stopping zookeeper: $zookeeperStop"
echo "************************************************************************************************************"
echo ">>>>>>>>>  2.Stopping zookeeper  <<<<<<<<<"
sh -c "$zookeeperStop" >> "$zookeeperLogs" 2>&1 &
#echo "zookeeper logs1" 2>&1 >$zookeeperLogs &
checkServerStopped "zookeeper" $hostName $zookeeperPort
########zookeeper end############

