# GameBattleDemo
This is a simple demo for a battle game using smartfoxserver.
There are 2 teams (Team A & Team B) with hardcode the army.
The army has 3 characters such as Heal, Strength, Shield.
Logic for calculating the heal of an army -> Heal = Heal - (strength - shield) For example: The Army A (Heal = 100, Strength = 50, Shield = 10) The Army B (Heal = 200, Strength = 60, Shield = 5) When A attacks B -> Heal of B = 200 - (50 - 5) = 155
If all the armies in a team are died (heal < 0), the team is defeated.
