# WorldBackup

**World Backup** is a simple **java based minecraft world backup tool** primarly intended to be used for backing up worlds on vanilla minecraft servers. The server must have rcon enabled in order to make this tool function correctly. Also since this is a scrpt like program you would probably need a task scheduler (ex. cron).

IMPORTANT: This program doesnt delete old backups automaticly, you will need to delete theme manualy, this is done in order to prevent older backups beeing lost.

## Getting Started

I will give you a short tutorial on how to set up this tool on a Debian distribution called Ubuntu Server (these steps should be same for any Debian based Linux distro). I recommend using Debian distribution called Ubuntu Server for any type of server hosting.
More detailed guide will be available here: [MinecraftWorldBackup](https://github.io/docs/worldbackup/index.html)

### Prerequisites

First of all we need to download (or make sure we have) Java 8 installed on our server instance. To do that type in:

```
sudo apt update
sudo apt install openjdk-8-jre
```

If the terminal returns:

```
$ openjdk-8-jre is already the newest version
```

You already had Java 8 installed (and that is expected since Minecraft server requries Java 8 too). 

### Installing

Now we need to setup our program to function correctly. First we need to gain sudo rights and to do that type:

```
sudo su
```

It will prompt you for your password so type it in.
We want to clone and rename our direcotry, so in order to do that type in:

```
git clone https://github.com/Pequla/WorldBackup
mv /WorldBackup/compiled /mcbackup
rm -R /MinecraftWorldBackup
```

Now we should make our jar and sh files executable on our server, in order to to that we will type in the following:

```
cd /mcbackup
chmod u+x WorldBackup.jar
chmod u+x backup.sh
```

You will need to edit the configuration filed, to do that type in:

```
nano config.properties
```

The configuration file looks like this by default:

NOTE: The program will not function correctly if you specify a path to the folder that doesnt exist, you need to manualy create a folder first, i recommend using a folder on another drive if possible.

```
#WorldBackup configuration file.
#Make sure you use / when specifying file paths !!!
#Created by: Pequla ( https://pequla.github.io/ )
#Thu Mar 12 23:38:50 CET 2020
rcon.password=password
source.folder=/home/username/minecraft/world
rcon.port=25575
output.path=/home/username/backups
rcon.host=localhost
```

After we done that, we will create a new cron task that will run our program every 30 minutes (but you can always change this to whatever you want).

NOTE: If you want to change the interval the backup starts i recommend just Googling cron tast every...

In order to do this we need to edit our cron tab.

```
crontab -e
```

Chose to use NANO text editor and go to the end of the document. At the end paste this:

```
*/30 * * * * /mcbackup/backup.sh
```
## Libraries

* [rkon-core](https://github.com/Kronos666/rkon-core) - Source RCON protocol library made by Kronos666

## Authors

* **Petar Kresoja** - *University Singidunum - Faculty of Informatics and Computing* - [Pequla](https://github.com/Pequla)

## License

This project has no licence but any kind of notice to the github page would be kindly appreciated. Thanks !!!