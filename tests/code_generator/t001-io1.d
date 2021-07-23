int main() {
    Print(f(5));
}

int f(int x) {
    if (x == 1)
        return 1;
    if (x == 0)
        return 1;

    return f(x-1) + f(x-2);
}