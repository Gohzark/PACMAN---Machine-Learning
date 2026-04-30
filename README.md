# README -- PACMAN Machine Learning

Ce projet permet d'exécuter quelques algorithmes d'apprentissage artificiel basés sur le Q-Learning dans le cadre du jeu PACMAN.

## Prérequis

- Java
- Python
- Matplotlib
- Pandas

## Usage 

Pour tester le code, il faut ouvrir le fichier "src/main/main_standardMode" puis choisir :

- Un niveau (0, 1 ou 2).
- Un algorithme à utiliser (0=Tabluar Q-Learning, 1=Approximate Q-Learning , 2= Approximate Q-Learning with Neural Network ou 3=Deep Q-Learning)
- Des hyperparamètres (epsilon, gamma, le taux d'apprentissage, nombre de parties joué en 1 génération et le nombre de génération).
- La version -> un numero à changer pour ne pas écraser les plots si on veut réutiliser un algorithme sur un même niveau mais avec des hyperparamètres différents.

Les graphiques générés par la classe "src/plot/PlotRunner" sont placés dans "outputs".
