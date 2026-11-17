

class Semaphore {
private int val;

public Semaphore(int v) {
val = v;
}

public synchronized void acquire() {
while(val <= 0) {
try {
wait();
}
catch(InterruptedException e){}
}
val--;
}

public synchronized void release() {
val++;
notify();
}
};
