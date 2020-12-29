# NASA Daily Chales maziarski 4A FSI2

## Présentation

**Projet Android de 4ème année à l'ESIEA.**

Petit projet démontrant l'utilisation du pattern MVC dans une application Android, codé en Kotlin.
<br/>Cette application permet l'affichage d'une liste des 10 dernières photos que la NASA poste tous les jours.
<br/>Vous y trouverez aussi une fonction de recherche par date pour revenir dans le temps ainsi qu'une fonction de recherche par titre.

Pour cela, l'application passe par une API que la NASA a ouvert au public.

## Consignes respectées : 

- Clean Architecture & ***MVC*** j'ai effectué une architecture MVC car j'ai eu plus de facilité avec, étant donné que j'ai appris cette architecture l'année dernière
Model : code qui s’occupe de la façon dont les données sont stockées
View : code qui s’occupe de la façon dont les données sont affichées
Controller : code qui qui s’occupe de la façon dont les données sont créées / mises à jour / supprimées

Pour moi,la principale différence entre les deux architectures réside dans la richesse et la complexité du ViewModel.
Dans un projet plus MVVM, le ViewModel est important. Il garde une trace de son propre état interne, c’est-à-dire que le client peut effectuer des manipulations de données temporaires qui ne sont pas communiquées au contrôleur. À titre d’exemple, les projets React et Angular entrent dans cette catégorie.

- Appels ***REST***
- Écrans : 3 activités
- Affichage d'une liste dans un ***RecyclerView***
- Affichage du détail d'un item de la liste
- ***Material Design*** propre

## Fonctions supplémentaires :
j'ai rajouté quelque fonction supplémantaire:
- Recherche par date<br/>
- Recherche par titre<br/>
- Stockage ***cache***<br/>
- Notifications via ***Firebase***<br/>
- Zoom sur les photos


## Fonctionnalités: 

### Écran de chargement

- Splash avec une fusée qui décolle 


### Écran d'accueil 

- Affiche la liste des images et leur titre. On peut aussi y choisir de rechercher une veille publication par sa date ou alors de chercher parmi les photos actuelles selon son titre.



### Écran du détail de la photo

- Affichage de l'image en entier
- Possibilité de zoomer sur l'image pour observer plus de détails
- Petit paragraphe explicatif en anglais sur le pourquoi du comment de la photo



### Selection par date

Si vous souhaitez pour retourner dans le temps et regarder une photo plus veille que les 10 photos affichées de base, vous pouvez utiliser le sélecteur de date en haut à droite de l'écran principal. 
Vous serez limité à 1996, aucune donnée n'étant disponible pour une date antérieure. 




### Notifications Firebase

Tous les jours, vous serez rappelé d'aller consulter l'image nouvellement postée par la NASA !



