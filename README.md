# Geographic Information
### Santiago Prado - A00365113 <br>Jeison Lasprilla - A00380415 <br>Juan Diego Lora - A00369885
### IDE: Intellij
# Menu
First show the menu with tree options.
* Read a sql command by console
* Read a sql commands in a sql file
* Exit
<br>

What do each option:
* _**Option [1]**_, reads the sql command. And pass it throw a filter, that determine whether the command is INSERT INTO, SELECT * FROM, or DELETE FROM, and do it the action. <br>
The countries are stored into an arraylist. Each country has another arraylist which contains its cities.<br>
* _**Option [2]**_, reads each line of the file. Pass it throw the same filter, and does the same action of the option **[1]**.

## Filters
To know which command is used we use a regex to match. And pass it when matches.

## Read
Every command read (which produces a change on the arraylists) save/serialize the data immediately in a txt on Json format. <br>
Write every object of the Country's arraylist into the txt file. 





