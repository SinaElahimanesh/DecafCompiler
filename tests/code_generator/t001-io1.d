
int a(int x) {
    Print(1);
    Print(x);
    b(x);
}

int main() {
   a(13);
}

void b(int x) {
    Print(2);
    Print(x);
}