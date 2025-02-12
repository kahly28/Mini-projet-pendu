Jeu du Pendu en Réseau

Réalisé par : Myriam MANSOURI et Ahmet KALYONCU


Ce projet consiste en la réalisation d’un jeu du pendu en réseau à l'aide de la programmation par sockets en Java. Deux modes de jeu sont proposés :

Mode solo : un client joue contre le serveur.
Mode multijoueur : plusieurs clients jouent les uns contre les autres.
Fonctionnalités principales

Serveur :
Gère les connexions et déconnexions.
Initialise et suit l'état des parties (mot à deviner, tentatives restantes, etc.).
Synchronise les joueurs et envoie des mises à jour sur l'état du jeu.
Gère les messages de chat entre les joueurs en mode multijoueur.
Client :
Choisit les paramètres du jeu (nombre de tentatives, durée maximale, mode de jeu).
Propose des lettres ou des mots pour deviner le mot mystère.
Interagit avec les autres joueurs via le chat intégré en mode multijoueur.
Extensions ajoutées

Chat intégré : communication en temps réel entre joueurs.
Chronomètre : limite le temps de jeu pour ajouter de la pression.
Gestion des salons : création et rejoignement de salons pour organiser des parties personnalisées.

