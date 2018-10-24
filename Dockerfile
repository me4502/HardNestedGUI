FROM kalilinux/kali-linux-docker
MAINTAINER Madeline Miller

RUN add-apt-repository ppa:linuxuprising/java \
  && sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EA8CACC073C3DB2A \
  && apt-get update \
  && echo oracle-java11-installer shared/accepted-oracle-license-v1-2 select true | /usr/bin/debconf-set-selections \
  && apt-get -y install oracle-java11-installer

ADD HardNestedGUI-*.jar app.jar

EXPOSE 5078

ENTRYPOINT java -jar app.jar