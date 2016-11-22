#include<stdio.h>
#include<string.h>
#include<stdlib.h>

#define MAX 512            /* Maksymalny rozmiar wczytywanego obrazu */
#define DL_LINII 1024      /* Dlugosc buforow pomocniczych */

/************************************************************************************
 * Funkcja wczytuje obraz PGM z pliku do tablicy       	       	       	       	    *
 *										    *
 * \param[in] plik_we uchwyt do pliku z obrazem w formacie PGM			    *
 * \param[out] obraz_pgm tablica, do ktorej zostanie zapisany obraz		    *
 * \param[out] wymx szerokosc obrazka						    *
 * \param[out] wymy wysokosc obrazka						    *
 * \param[out] szarosci liczba odcieni szarosci					    *
 * \return liczba wczytanych pikseli						    *
 ************************************************************************************/

int czytaj(FILE *plik_we,int obraz_pgm[][MAX],int *wymx,int *wymy, int *szarosci) {
  char buf[DL_LINII];      /* bufor pomocniczy do czytania naglowka i komentarzy */
  int znak;                /* zmienna pomocnicza do czytania komentarzy */
  int koniec=0;            /* czy napotkano koniec danych w pliku */
  int i,j;

  /*Sprawdzenie czy podano prawidłowy uchwyt pliku */
  if (plik_we==NULL) {
    fprintf(stderr,"Blad: Nie podano uchwytu do pliku\n");
    return(0);
  }

  /* Sprawdzenie "numeru magicznego" - powinien być P2 */
  if (fgets(buf,DL_LINII,plik_we)==NULL)   /* Wczytanie pierwszej linii pliku do bufora */
    koniec=1;                              /* Nie udalo sie? Koniec danych! */

  if ( (buf[0]!='P') || (buf[1]!='2') || koniec) {  /* Czy jest magiczne "P2"? */
    fprintf(stderr,"Blad: To nie jest plik PGM\n");
    return(0);
  }

  /* Pominiecie komentarzy */
  do {
    if ((znak=fgetc(plik_we))=='#') {         /* Czy linia rozpoczyna sie od znaku '#'? */
      if (fgets(buf,DL_LINII,plik_we)==NULL)  /* Przeczytaj ja do bufora                */
	koniec=1;                   /* Zapamietaj ewentualny koniec danych */
    }  else {
      ungetc(znak,plik_we);                   /* Gdy przeczytany znak z poczatku linii */
    }                                         /* nie jest '#' zwroc go                 */
  } while (znak=='#' && !koniec);   /* Powtarzaj dopoki sa linie komentarza */
                                    /* i nie nastapil koniec danych         */

  /* Pobranie wymiarow obrazu i liczby odcieni szarosci */
  if (fscanf(plik_we,"%d %d %d",wymx,wymy,szarosci)!=3) {
    fprintf(stderr,"Blad: Brak wymiarow obrazu lub liczby stopni szarosci\n");
    return(0);
  }
  /* Pobranie obrazu i zapisanie w tablicy obraz_pgm*/
  for (i=0;i<*wymy;i++) {
    for (j=0;j<*wymx;j++) {
      if (fscanf(plik_we,"%d",&(obraz_pgm[i][j]))!=1) {
	fprintf(stderr,"Blad: Niewlasciwe wymiary obrazu\n");
	return(0);
      }
    }
  }
  return *wymx**wymy;   /* Czytanie zakonczone sukcesem    */
}                       /* Zwroc liczbe wczytanych pikseli */


/* Wyswietlenie obrazu o zadanej nazwie za pomoca programu "display"   */
void wyswietl(char *n_pliku) {
  char polecenie[DL_LINII];      /* bufor pomocniczy do zestawienia polecenia */

  strcpy(polecenie,"display ");  /* konstrukcja polecenia postaci */
  strcat(polecenie,n_pliku);     /* display "nazwa_pliku" &       */
  strcat(polecenie," &");
  printf("%s\n",polecenie);      /* wydruk kontrolny polecenia */
  system(polecenie);             /* wykonanie polecenia        */
}

char* wczytaj(FILE *plik, int obraz[MAX][MAX], int wymx, int wymy, int odcieni)
{
  int odczytano = 0;
  char nazwa[100];
  int i=0;

  /* Wczytanie zawartosci wskazanego pliku do pamieci */
  printf("Podaj nazwe pliku:\n");
  scanf("%s",&nazwa);
  plik=fopen(nazwa,"r");

  if (plik != NULL) {       /* co spowoduje zakomentowanie tego warunku */
    odczytano = czytaj(plik,obraz,&wymx,&wymy,&odcieni);
    fclose(plik);
    i++;
  }

  /* Wyswietlenie poprawnie wczytanego obraza zewnetrznym programem */
  return nazwa;
}

int zapisz(FILE *plik_z, int obraz_pgm[][MAX], int *wymy, int *wymx, int odcieni) { 
  int i,j;
  fprintf(plik_z, "P2\n");
  fprintf(plik_z, "%d %d %d\n", *wymx, *wymy, odcieni);
  for(i=0;i<*wymy;i++){
    for(j=0;j<*wymx;j++) 
      fprintf(plik_z, "%d ", obraz_pgm[i][j]);}

  /* Wczytanie zawartosci wskazanego pliku do pamieci */
    if (plik_z = NULL)
	printf("Brak wczytanego pliku\n");
}

int wyswietl_z() { 
  int odczytano = 0;
  FILE *plik;
  char nazwa[100];
  int k=1;
  char wybor_z = 'k';

  for (k; k>0;k++){
  while (wybor_z != ' '){
    printf("Zapisane pliki:\n");
    printf(" %d - %s\n", k, nazwa);
    printf("Twoj wybor:");
    scanf("%1s",wybor_z);
    switch (wybor_z){
     
      case 'k': printf("%s", nazwa);
    } }}}
int main (char *argv[]) {
  char wybor[] = " ";
  FILE *plik = NULL;
  int obraz[MAX][MAX];
  int wymx,wymy,odcieni;
  int k=0;
  int i=0;
  char *nazwaplik;
  char nazwa[100];
  char wybor_z = 'k';


  while (wybor[0] != '5'){
      printf("Menu:\n");
      printf(" 1 - Wczytanie zawartosc wskazanego pliku\n");
      printf(" 2 - Zapisz plik\n");
      printf(" 3 - Wyświetlenie plikow z pamieci\n");
      printf(" 4 - Przetwarzanie obrazu\n");
      printf(" 5 - Zamknij program\n");
      printf(" Twoj wybor: ");
      scanf("%1s", wybor);
    
    switch (wybor[0])
	{
      case '1':
        nazwa = wczytaj(plik,obraz,wymx,wymy,odcieni);
        break;
      case '2':
        if ( i !=0)
          {
            printf("Podaj nazwe pliku:");
            scanf("%s",nazwaplik);
            zapisz(plik, obraz, &wymx, &wymy, odcieni);
            fclose(plik);
            printf("Zapisano plik %s\n", nazwaplik);
            k++;
        }
        else puts("Brak pliku, wczytaj plik");
          break;
      case '3':
        for (k; k>0;k++){
          while (wybor_z != ' '){
            printf("Zapisane pliki:\n");
            printf(" %d - %s\n", k, nazwa);
            printf("Twoj wybor:");
            scanf("%1s",wybor_z);
            switch (wybor_z){

            case 'k': printf("%s", nazwa);
            } } }
          break;
	}
}
  return 0;
}