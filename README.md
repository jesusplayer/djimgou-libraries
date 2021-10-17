#**CarRent**

Projet de visite de vehicule

#Configuration

**NB:** Avant de commencer il est important de vous rassurer que le chemin absolu de ce projet ne contient pas d'espace, sinon les commandes maven ne marcheront pas

## Exécution du projet

Le projet tourne via *SpringBoot* en utilisant la commande:

* `run-dev.cmd`: Pour exécuter en mode déveleppement. se rassurer d'avoir une BD MySQL installée. voir le fichier `src\main\resources\application-dev.properties`

* `run-test.cmd`: Exécuter le projet en mode test. Dans ce mode, pas besoin d'installer une base de données, car il télécharge la base de données H2 qui s'exécute en memoire vive. voir le fichier `src\main\resources\application-test.properties`

* `run-stag.cmd`: Exécuter le projet en mode préprod sur la plateforme cloud heroku. Voir le fichier `src\main\resources\application-stag.properties`

* `start-preprod.bat`: Exécuter le projet en mode pre production sous ORACLE. voir le fichier `src\main\resources\application-prod.properties`
  
* `start-prod.bat`: Exécuter le projet en mode production sous ORACLE. voir le fichier `src\main\resources\application-prod.properties`

* `start.bat`: Exécuter le projet selon le mode configuré dans la machine. il se charge en même temps de configuré l'exécution automatique lors du redemarage de la machine

* `mvn clean package && java -jar target/carrent.war`: Exécute manuellement l'application

## Déploiement du projet
* Configurer le fichier de `src/main/resources/application-prod.properties` et se rassurer que les paramètres de la base de donnée de production sont bien renseignés (Lignes 22 à 25) ensuite modifier au besoin les autres champs
        
        server.port                     // Port de l'application CARRENT
        server.address                  // Adresse de l'application CARRENT
        spring.datasource.url           // url de la BD
        spring.datasource.username      // utilisateur ou Schema de la BD
        spring.datasource.password      // Mot de passe de l'utilisateur
  
* Lorsque l'application est déjà en production il faut utiliser les configurations dans le dossier `target\config`. Après modification, redemarrer l'application

* `build.cmd`: Package le projet et génère le fichier `target\carrent-distribution.zip`. Après avoir dézipper ce fichier, le dossier contient les commandes `start-preprod.bat`, `start-prod.bat` ces commandes permettent respectivement de lancer le serveur en preprod ou en prod. A defaut d'utiliser cette commande vous pouvez modifier le code à votre guise pour choisir le profile(dev,prod,preProd, etc.)

* `push-preprod.cmd`: Copy le fichier `target\carrent-distribution.zip` vers le serveur preprod `\\CARRENTSERVER\Users\Administrateur\Documents\CARRENT\app`

* `deploy-preprod.cmd`: deploy le projet directement vers le serveur preprod en associant `build.cmd` et `push-preprod.cmd`
  
L'application est disponible sur l'adresse selon la valeur `http://${server.address}:${server.port}`


## Exécution du projet en production

Après déploiement, il est important d'exécuter le projet sur un environnement de console optimisée pour faciliter la haute disponibilité de l'application.
Pour cela nous utilisons l'outil Cmder téléchargeable ici [Cmder](https://github.com/cmderdev/cmder/releases/download/v1.3.17/cmder_mini.zip)

Pour permettre une indépendance totale, `Cmder` est directement intégré dans le package de CARRENT dans le dossier `cmder_mini`. Donc plus besoin de l'installer. 
Cependant nous devons configurer une tâche de lancement automatique dans Cmder. pour celà,
   
- lancer `cmder_mini\Cmder.exe`. sur le boutton vert + du bas vers la droite, cliquer sur le menu déroulant et choisir `Setup tasks` 

![](img/cmder1.bmp)

 - Renseigner les informations comme indiqué sur l'image suivante

![](img/cmder2.PNG)

1. Choisir {cmd::Cmder}
2. choisir une icone (optionnelle, vous pouvez passer cette étape)
3. mettre le chemin d'accès à la commande de lancement de FRM en l'occurence
          
        cmd /k ""C:\Users\Administrateur\Documents\CARRENT\app\target\start-prod.bat" "

4. Enregistrer 


5. Fermer la console puis double cliquer sur la commande `start.bat` . 
   La commande `start.bat` est celle qui se chargera de lancer automatiquement CARRENT en cas de redemarrage de la machine

L'application est disponible sur l'adresse selon la valeur `http://${server.address}:${server.port}`

## Prerequis
1. JDK 1.8
2. maven 3 (mais l'IDE ) [intellij Idea](https://www.jetbrains.com/fr-fr/idea/) intègre par défaut maven
    
    Dans le cas de l'utilisation d'un proxy, par exemple celui de la banque, créer un fichier  `C:\Users\NOM_USER\.m2\settings.xml` avec pour contenu
    
   ```
    <settings>
      <proxies>
       <proxy>
          <id>example-proxy</id>
          <active>true</active>
          <protocol>http</protocol>
          <host>192.20.165.22</host>
          <port>8080</port>
       </proxy>
       <proxy>
          <id>example-proxy2</id>
          <active>true</active>
          <protocol>https</protocol>
          <host>192.20.165.22</host>
          <port>8080</port>
        </proxy>
      </proxies>
    </settings>
    ```
   
## Configuration de l'IDE 

* Utiliser [intellij Idea](https://www.jetbrains.com/fr-fr/idea/)
* Installer le plugin lombock:
    
    Aller dans File->Settings->Plugings->Marketplace, rechercher lombock, l'installer et redemarer l'IDE
    
    NB : Se rassurer de configurer le proxy au cas où.
    
* Installer le plugin [Database Navigator](https://plugins.jetbrains.com/plugin/1800-database-navigator/)

  Il permet de se connecter aux bases de données et de les administrer directement à partir de Intellij  


## Gestion des logs
Log4j2 est utilisée pour gérer les logs. Le configurer dans le fichier `src\main\resources\log4j2.xml` 

Pour utiliser les logs dans le code, pour chaque classe mettre l'annotation `@Log4j2(topic = "NOM_DU_TOPIC")`
```java
@Log4j2(topic = "NOM_DU_TOPIC")
public class MaClasse{

    public void maFunctionLog(){
        log.debug("Debugging log");
        log.info("Info log");
        log.warn("Hey, This is a warning!");
        log.error("Oops! We have an Error. OK");
        log.fatal("Damn! Fatal error. Please fix me.");
    }   

}
```  