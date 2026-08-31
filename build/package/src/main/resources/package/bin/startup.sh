#!/bin/sh

#======================================================================
# 项目启动shell脚本
# boot目录: spring boot jar包
# config目录: 配置文件目录
# logs目录: 项目运行日志目录
# logs/startup.log: 记录启动日志
# logs/back目录: 项目运行日志备份目录
# nohup后台运行
#
# author: 0neBean
# date: 2018-12-2
#======================================================================

# 项目名称
APPLICATION="@project.name@"

# 环境
ENVIRONMENT="@profileActive@"

# 项目启动jar包名称
APPLICATION_JAR="@build.finalName@.jar"

# bin目录绝对路径
BIN_PATH=$(cd `dirname $0`; pwd)
# 进入bin目录
cd `dirname $0`
# 返回到上一级项目根目录路径
cd ..
# 打印项目根目录绝对路径s
# `pwd` 执行系统命令并获得结果
BASE_PATH=`pwd`

# 项目日志输出绝对路径
LOG_DIR=${BASE_PATH}"/logs"
# 日志备份目录
LOG_BACK_DIR="${LOG_DIR}/startup-backup/"

# 项目启动日志输出绝对路径
LOG_STARTUP_PATH="${LOG_DIR}/app.log"

# 当前时间
NOW=`date +'%Y-%m-%m-%H-%M-%S'`
NOW_PRETTY=`date +'%Y-%m-%m %H:%M:%S'`

# 启动日志
STARTUP_LOG="\n\n\n================================================ APPLICATION STARTUP ON [${NOW_PRETTY}] ================================================\n\n\n"

# 如果logs文件夹不存在,则创建文件夹
if [ ! -d "${LOG_DIR}" ]; then
  mkdir "${LOG_DIR}"
fi

# 如果logs/back文件夹不存在,则创建文件夹
if [ ! -d "${LOG_BACK_DIR}" ]; then
  mkdir "${LOG_BACK_DIR}"
fi

# 如果项目运行日志存在,则重命名备份
if [ -f "${LOG_STARTUP_PATH}" ]; then
	mv ${LOG_STARTUP_PATH} "${LOG_BACK_DIR}/startup_backup_${NOW}.log"
fi

# 创建新的项目运行日志
echo "" > ${LOG_STARTUP_PATH}

#==========================================================================================
# JVM Configuration
# -Xmx256m:设置JVM最大可用内存为256m,根据项目实际情况而定，建议最小和最大设置成一样。
# -Xms256m:设置JVM初始内存。此值可以设置与-Xmx相同,以避免每次垃圾回收完成后JVM重新分配内存
# -Xmn512m:设置年轻代大小为512m。整个JVM内存大小=年轻代大小 + 年老代大小 + 持久代大小。
#          持久代一般固定大小为64m,所以增大年轻代,将会减小年老代大小。此值对系统性能影响较大,Sun官方推荐配置为整个堆的3/8
# -XX:MetaspaceSize=64m:存储class的内存大小,该值越大触发Metaspace GC的时机就越晚
# -XX:MaxMetaspaceSize=320m:限制Metaspace增长的上限，防止因为某些情况导致Metaspace无限的使用本地内存，影响到其他程序
# -XX:-OmitStackTraceInFastThrow:解决重复异常不打印堆栈信息问题
#==========================================================================================


if [ -z "$JAVA_OPTS" ]; then
  JAVA_OPT="-Xms3G -Xmx6G -Xss1024K -XX:PermSize=256m -XX:MaxPermSize=512m -XX:-UseGCOverheadLimit -XX:-OmitStackTraceInFastThrow"
fi


#=======================================================
# 将命令启动相关日志追加到日志文件
#=======================================================

# 输出项目名称
STARTUP_LOG="${STARTUP_LOG}application name: ${APPLICATION}\n"
# 输出jar包名称
STARTUP_LOG="${STARTUP_LOG}application jar  name: ${APPLICATION_JAR}\n"
# 输出项目根目录
STARTUP_LOG="${STARTUP_LOG}application root path: ${BASE_PATH}\n"
# 输出项目bin路径
STARTUP_LOG="${STARTUP_LOG}application bin  path: ${BIN_PATH}\n"
# 打印日志路径
STARTUP_LOG="${STARTUP_LOG}application log  path: ${LOG_STARTUP_PATH}\n"
# 打印JVM配置
STARTUP_LOG="${STARTUP_LOG}application JAVA_OPTS : ${JAVA_OPTS}\n"
# 打印启动命令
STARTUP_LOG="${STARTUP_LOG}application startup command: nohup java ${JAVA_OPTS} -jar ${BASE_PATH}/boot/${APPLICATION_JAR}> >/dev/null 2>&1\n"

STARTUP_LOG="${STARTUP_LOG}application startup script execute done !\n"

#======================================================================
# 执行启动命令：后台启动项目,并将日志输出到项目根目录下的logs文件夹下
#======================================================================
nohup java ${JAVA_OPTS} -jar -Denv=${ENVIRONMENT} -Dversion=${APP_INST_VERSION} -Dport=${APP_INST_PORT} -DappKey=${APP_KEY} -DmachineId=${MACHINE_ID} -Dspring.profiles.active=${ENVIRONMENT} ${BASE_PATH}/boot/${APPLICATION_JAR} >/dev/null 2>&1 &


# 进程ID
PID=$(ps -ef | grep "${APPLICATION_JAR}" | grep -v grep | awk '{ print $2 }')
STARTUP_LOG="${STARTUP_LOG}application pid: ${PID}\n"

# 启动日志追加到启动日志文件中
echo -e ${STARTUP_LOG} >> ${LOG_STARTUP_PATH}

# 打印项目日志
if [ '@project.name@' != 'com.tkfc.gateway.web' ]; then
  tail -f ${LOG_STARTUP_PATH}
fi

