# pld-smartCity

Documentation de déploiement :

-Le client Android requiert une clé d'API pour pouvoir faire usage des API google maps et google calendar d'une part, et une deuxième clé d'API pour google geocoding d'autre part.
Les informations d'activation de l'API sont disponibles ici :
https://developers.google.com/maps/documentation/android/start
https://developers.google.com/maps/documentation/geocoding/

Le client peut être déployé sur un téléphone physique, cependant cela empêche d'utiliser notre appli sur un serveur déployé en localhost, et requiert donc de déployer la partie serveur sur une interface publique.
Une alternative est d'utiliser l'émulateur fourni par Google, see http://developer.android.com/sdk/index.html 
L'émulateur n'est pas distribué séparément de l'IDE mais peut être lancé en standalone via une interface en lignes de commande.
Notre appli fait usage des API Google et doit donc être installée sur une image système Android avec ces API actives (mention 'Google APIs' dans la description de l'image système). 
Toutes les URL sont en dur dans le code et pointent vers 10.0.2.2, c'est-à-dire le localhost du système hôte.


-La classe main du serveur logiciel est ServerController.java. Quoique nous n'ayons testé que sous windows, il est écrit en java et devrait en théorie fonctionner sur d'autres plateformes.
Cependant, il repose sur un serveur MySQL CE, qui n'utilise pas la même syntaxe sous Unix et sous Windows, aussi il est recommandé de le déployer sur un windows. 
Le script createTestDB.sql à la racine du dépôt Git permet de créer une DB de test.
Dans le cas d'une installation préexistante d'un serveur MySQL, penser à modifier les identifiants donnés à notre serveur pour qu'il puisse se connecter à la DB (en dur dans le code).
Pour les config de test, le serveur est lancé et arrêté après 20 secondes, cela permet de déployer, lancer quelques tests, puis redéployer au prochain changement sans se préoccuper d'arrêter le serveur. Cela peut être désactivé en enlevant les lignes Thread.sleep(20) et server.stop() à la fin du main.

Les tout derniers commit's sur le github ont été faits trop tard et n'ont pas été testés correctement. La dernière version stable et testée est la 737a2c22bdf76b8631d704640340a54c4ebfa15e (message de commit : Modifying views, author : yannvuadel).

Usage : 
Déployer le serveur MySQL, notre serveur logiciel, et le logiciel client Android. It should work. Fingers crossed.