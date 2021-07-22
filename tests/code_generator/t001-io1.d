
int a(int x) {
    Print(1);
    Print(x);
    b(x, 14);
}

int main() {
   a(13);
}

void b(int x, int y) {
    Print(2);
    Print(x);
    Print(3);
    Print(y);
}