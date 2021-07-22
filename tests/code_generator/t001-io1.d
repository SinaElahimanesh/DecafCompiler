int a(int x) {
    Print(1);
    Print(x);
    b(x, (100.0));
}

int main() {
   Print(2 + 3 + 5);
   Print(2 - 3 + 6 * 10);
   Print(1 - 2 + 3 - 4);
   Print(5 / 2);
   Print(5 * 2);
   Print(5 % 2);
   Print((3+5)/2);
   Print((3+5)/-2);
   Print((3+5)/--2);
   Print((3+5)/---2); 
}

void b(int x, double y) {
    Print(0XE2);
    Print(x);
    Print(3);
    Print(y);
}