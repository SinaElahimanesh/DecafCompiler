int a(int x) {
    Print(1);
    Print(x);
    b(x, (100.0));
}

int main() {
   a(13);
}

void b(int x, double y) {
    Print(0XE2);
    Print(x);
    Print(3);
    Print(y);
}