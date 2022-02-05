# GameBattleDemo
+ This is a simple demo for a battle game (PVE) using smartfoxserver.<br/>
+ A Room on Smartfoxserver is looked as a battle.The player joined and the battle will process
+ There are 2 teams (Team A & Team B) with hardcode the army.<br/>
+ The army has 3 characters such as Heal, Strength, Shield.<br/>
+ Logic for calculating the heal of an army -> Heal = Heal - (strength - shield)<br/>
      For example: The Army A (Heal = 100, Strength = 50, Shield = 10) <br/>
      The Army B (Heal = 200, Strength = 60, Shield = 5) <br/>
      When A attacks B -> Heal of B = 200 - (50 - 5) = 155<br/>
+ If all the armies in a team are died (heal <= 0), the team is defeated.
