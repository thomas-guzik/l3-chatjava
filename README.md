# l3-chatjava
Application java permettant un chat sur un un même réseau.

Les packages découpent 2 applications différentes.

La première application est dans le package istic.pr.socket.tcp.thread.
Elle se compose de 3 fichiers :
- ServeurTCP.java : Reçoit les messages des différents clients et les redistribue.
- TraiteUnClient.java : Aide ServerTCP à la gestion des clients
- ClientTCP.java : Envoie des messages au serveur

La seconde application est dans le package istic.pr.socket.udp.chat
Elle est composé que d'un seul fichier (ChatMulticast.java) permettant l'envoi et la réception des messages.
