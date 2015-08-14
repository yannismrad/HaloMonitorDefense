# Halo Monitor Defense

##Présentation
Halo Monitor Defense est un prototype de jeu de type Tower defense réalisé sous Android avec OpenGL ES.
Ce jeu constitue mon projet de fin d'année de Licence Informatique à l'Université de Limoges, l'objectif était de développer une application 3D sous Android sous une forme de notre choix.

##Scénario
Le scénario est inspiré du background de la série de jeux vidéos Halo. Vous incarnez un monitor chargé de défendre une installation face à la menace des Floods, des organismes parasitaires qui se sont échappés de leurs chambres et qui cherchent à rejoindre la surface de l'installation. Vous disposez de tourelles à energie possédant des caractéristiques différentes et devez les placer judicieusement pour éliminer les Floods avant qu'ils ne s'échappent.

##Technologie
L'application a été développée à l'aide du framework JPCT-AE permettant de simplifier certains aspects relatifs à l'utilisation d'OpenGL ES sous Android.

##Aspects développés
###Map et éléments de gameplay visibles
La map de jeu est basique, constituée d'un sol, un plan d'eau en background, d'emplacements destinés à accueillir les tours qui défendront un chemin central emprunté par les ennemis. Ces derniers apparaissent à un point de départ, et se dirigent vers le point de sortie.

###Génération des ennemis
Le jeu fonctionne par vagues d'ennemis. Entre chaque vague, un délai de temps permet de travailler sur les défenses mises en place.
Les vagues d'ennemis sont définies dans un fichier XML, parsé au début de la partie. Chaque vague contient un ou plusieurs types d'ennemis présents en un certain nombre. Les ennemis apparaissent dans un ordre aléatoire.

###Pathfinding
Le chemin est constitué de cases qui sont liées entre elles pour former une "liste chainée" (chaque case connait ses cases adjacentes), un ennemi qui avance sur une case va chercher quelle est la case suivant celle où il se trouve actuellement et va s'y déplacer.

###Placement des tours
Les blocs servants à construire des tours sont placés à intervalles réguliers le long du chemin, en fonction de sa longueur.
Toucher un bloc fait apparaitre un menu permettant de choisir un type de tour à placer (normale, tirs rapides, tirs puissants...).
Chaque bloc est associé à une case du chemin emprunté par les ennemis et possède une liste de cases atteignables par ses projectiles (sa portée de tir).

###Détection des ennemis par les tours, phase de tir
Lorsqu'un ennemi arrive sur une case du chemin, une vérification est effectuée pour savoir si cette case fait partie de la portée de tir d'une tour placée. Si c'est le cas, et si cette dernière n'est pas déjà en train de tirer sur un ennemi, celui qui est arrivé à portée est alors pris pour cible, jusqu'à sa mort ou jusqu'à ce qu'il sorte de la portée de tir.


