# Kauppalista
Useamman käyttäjän yhteinen, helposti päivitettävä selainpohjainen kauppalista.
Käyttäjillä voi olla useampi ostoslista käytössä ja kaikkiin ostoslistoihin voi liittyä useampi käyttäjä.

### Kauppalistan luokkakaavioluonnos
[Luokkakaavio](https://drive.google.com/file/d/0B6Spnoit3lkycnF1dGx3R1NSM0k/view?usp=sharing)

Kauppalista-sovelluksessa käytettävät polut:
/kauppalista - GET - palauttaa listan kauppalistoja
/kauppalista/{id} - GET - näyttää yhden kauppalistan
/kauppalista/{id}/save - POST - siirtää kauppalistan historiaan ja ohjaa uudelleen osoitteeseen ”/kauppalista”

