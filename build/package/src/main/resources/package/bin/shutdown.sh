#!/bin/sh

#======================================================================
# 项目停服shell脚本
# 通过项目名称查找到PID
# 然后kill -9 pid
#
# author: 0neBean
# date: 2018-12-2
#======================================================================
# 进入bin目录
cd `dirname $0`
# 返回到上一级项目根目录路径
cd ..
# 打印项目根目录绝对路径
# `pwd` 执行系统命令并获得结果
BASE_PATH=`pwd`
# 项目名称
APPLICATION="@project.name@"
# 项目日志输出绝对路径
LOG_DIR=${BASE_PATH}"/logs"
LOG_FILE="app.log"
LOG_PATH="${LOG_DIR}/${LOG_FILE}"
# 当前时间
NOW_PRETTY="$(date +%F) $(date +%T)"
RESTART_SH_PATH=${BASE_PATH}/bin/restart.sh
RESTART_SHUTDOWN_PATH=${BASE_PATH}/bin/shutdown.sh
# 启动日志
SHUTDOWN_LOG="\n\n\n================================================  APPLICATION SHUTDOWN ON [${NOW_PRETTY}]  ================================================\n\n\n"

PID=$(ps -ef | grep "${BASE_PATH}" | grep -v grep | grep -v "${RESTART_SH_PATH}"| grep -v "${RESTART_SHUTDOWN_PATH}" |awk '{ print $2 }')
if [ -z "$PID" ]
  then
      echo ${APPLICATION} is already stopped
  else
      echo kill  ${PID}
      kill -9 ${PID}
      echo ${SHUTDOWN_LOG} >> ${LOG_PATH}
      echo ${APPLICATION} stopped successfully
  fi
