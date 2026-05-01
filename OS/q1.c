#include <stdio.h>
int main()
{
    int n, tq, i, time = 0, remain;
    printf("Enter number of processes: ");
    scanf("%d", &n);
    int bt[n], rt[n];
    printf("Enter burst times:\n");
    for (i = 0; i < n; i++)
    {
        scanf("%d", &bt[i]);
        rt[i] = bt[i];
    }
    printf("Enter time quantum: ");
    scanf("%d", &tq);
    remain = n;
    while (remain != 0)
    {
        for (i = 0; i < n; i++)
        {
            if (rt[i] > 0)
            {
                if (rt[i] <= tq)
                {
                    time += rt[i];
                    rt[i] = 0;
                    remain--;
                }
                else
                {
                    rt[i] -= tq;
                    time += tq;
                    printf("Process %d finished at time %d\n", i + 1, time);
                }
            }
        }
    }
    return 0;
}