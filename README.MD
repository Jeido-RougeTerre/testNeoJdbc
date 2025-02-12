# JDBC In Neoforge Example mod
![Available for 1.21.1](https://img.shields.io/badge/Available_for-1.21.1-orange)
![NeoForge 21.1.94](https://img.shields.io/badge/NeoForge-21.1.94-orange)
[![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)][MySQL]

This is an example mod using JDBC in neoforge. It uses MySQL but can easily be changed.

See MySQL Documentation [here][MySQL].

## Requirements
It needs a valid driver jar in mods for MySQL. [download it here](https://modrinth.com/mod/mysql-driver).

Put the Jar in ``/run/mods`` to be able to use the driver.

by default the database information are :

- **URL** : ``jdbc:mysql://localhost:3306/mcdb``
- **Username** : ``root``
- **Password** : ``root``

## Commands
There is a command for you to test

``/player``

[MySQL]: https://dev.mysql.com/doc/connector-j/en/