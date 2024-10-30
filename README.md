# Gestion des Contacts Android

## Description
Cette application Android permet de gérer une liste de contacts avec des fonctionnalités de recherche, ajout, suppression et affichage de contacts. Elle inclut également un écran de démarrage avec animation.

### Fonctionnalités principales :
- **Affichage de contacts** : Récupère les contacts depuis le téléphone et les affiche dans une liste.
- **Recherche de contacts** : Permet de rechercher un contact par nom.
- **Ajout de contact** : Ajoute un nouveau contact dans le téléphone.
- **Suppression de contact** : Supprime un contact en glissant vers la gauche et en confirmant via une boîte de dialogue.
- **Ecran de démarrage (Splash Screen)** : Animation de démarrage pour une meilleure expérience utilisateur.

## Structure du projet
1. **MainActivity** : Activité principale qui gère l'affichage des contacts, la recherche, l'ajout et la suppression.
2. **ContactAdapter** : Adaptateur pour gérer et afficher les contacts dans un `RecyclerView`.
3. **Contact** : Modèle de données représentant chaque contact.
4. **SplashActivity** : Activité de démarrage avec une animation pour un écran de démarrage personnalisé.

## Configuration du projet

### Prérequis
- Android Studio
- Permissions pour accéder aux contacts dans les paramètres de l'application.

### Installation
1. Cloner le projet sur votre machine locale 
2. Ouvrir le projet avec Android Studio.
3. Construire et exécuter l'application sur un émulateur ou un appareil Android.

## Permissions
L'application nécessite la permission `READ_CONTACTS` pour accéder et lire les contacts du téléphone. Cette permission est demandée lors du lancement de l'application.

## Utilisation
1. **Affichage de la liste des contacts** : Les contacts de l'appareil sont automatiquement chargés et affichés dans une liste.
2. **Ajout d'un contact** :
   - Appuyer sur le bouton d'ajout.
   - Remplir les informations du contact dans la boîte de dialogue et confirmer.
3. **Suppression d'un contact** :
   - Glisser vers la gauche pour supprimer un contact.
   - Confirmer la suppression dans la boîte de dialogue.
4. **Recherche de contacts** : Utiliser la barre de recherche pour filtrer les contacts par nom.

## Démo vidéo et Screens 


https://github.com/user-attachments/assets/8eca6bc3-a90f-4ad9-a17d-f3f760b8fcc5



