#include<stdio.h>
#include<stdlib.h>
#include<string.h>

void setup() {
	setvbuf(stdin, NULL, _IONBF, 0);
	setvbuf(stdout, NULL, _IONBF, 0);
	setvbuf(stderr, NULL, _IONBF, 0);
}

struct sinhVien {
    char *name;
    unsigned int age;
    float score;
    struct sinhVien *next;
};

typedef struct sinhVien sinhVien;

int my_read(char buf[],unsigned int size){
    int len ,i ;
    len = read(0,buf,size) ;
    for(i =0 ;i<len;i++){
        if(buf[i]=='\n'){
            buf[i] = NULL ;
        }
    }
    return len ;
}

void menu() {
    puts("1. Add student");
    puts("2. Print student");
    puts("3. Delete student");
    puts("4. Exit");
    printf("> ");
}

sinhVien *makeSV() {
    sinhVien *newSV = (sinhVien*)malloc(sizeof(sinhVien));
    unsigned int size ;
    printf("Size name: ");
    scanf("%u",&size) ;
    newSV->name = malloc(size); 
    printf("Name: ");
    my_read(newSV->name,0x1337) ;
    printf("Age: ");
    scanf("%u", &newSV->age);
    printf("Score: ");
    scanf("%f", &newSV->score);
    getchar();
    newSV->next = NULL;
    return newSV;
}

void add(sinhVien** top) {
    sinhVien* newnode = makeSV();
    if (*top == NULL) {
        *top = newnode;
    } else {
        sinhVien* tmp = *top;
        while (tmp->next != NULL) {
            tmp = tmp->next;
        }
        tmp->next = newnode;
    }
}

void show(sinhVien* top) {
    unsigned int idx = 0;
    puts("$$$$$ KCSC SCORE $$$$$");
    while (top != NULL) {
        printf("ID: %d\nNAME: %s\nAGE: %d\nSCORE: %.2f\n\n", idx, top->name, top->age, top->score);
        top = top->next;
        idx++;
    }
    puts("$$$$$$$$$$$$$$$$$$$$$$");
}

void delete(sinhVien **head, unsigned int idx) {
    if (*head == NULL) {
        printf("List is empty. Cannot delete.\n");
        return;
    }
    sinhVien *temp = *head;
    if (idx == 0) {
        *head = temp->next; 
        free(temp->name);
        free(temp);
        printf("Successfull.\n");
        return;
    }
    sinhVien *prev = NULL;
    for (unsigned int i = 0; i < idx; i++) {
        if (temp == NULL) {
            printf("Invalid index.\n");
            return;
        }
        prev = temp;
        temp = temp->next;
    }
    if (temp == NULL) {
        printf("Invalid index.\n");
        return;
    }
    prev->next = temp->next;
    free(temp->name);
    free(temp);
    printf("Successfull.\n");
}

int main() {
    setup();
    puts("Welcome to KCSC Score !!!");
    sinhVien *head = NULL;
    unsigned int choice;
    while (1) {
        menu();
        scanf("%u", &choice);
        getchar(); 
        switch (choice) {
            case 1:
                add(&head);
                break;
            case 2:
                show(head);
                break;
            case 3:
                printf("Index: ");
                scanf("%u", &choice);
                getchar(); 
                delete(&head,choice);
                break;
            case 4:
                exit(0);
            default:
                puts("Invalid choice.");
                break;
        }
    }
}
