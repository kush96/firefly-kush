# Top Words Fetcher
##### Kush Singh Assignment Submission
My submisssion for the assignment [here](https://docs.google.com/document/d/1GdLjenD201pNl3LOmvaBM0a56DIIEC88fBd3kimBPrQ/edit?pli=1).
### Installation

- Install [Docker](https://www.docker.com/get-started/)
- On terminal
<pre>
# Clone the repository
git clone [https://github.com/username/repository.git](https://github.com/kush96/firefly-kush.git)

# Change into the repository directory
cd firefly-kush

# Build Docker Image
docker build -t myapp .

# Run Docker Container, with the number of URLs you want to process
docker run -it myapp 100

# At end of execution, a pretty json will be printed 
✨Magic ✨
</pre>

### Workflow Diagram

![Workflow Diagram](https://i.postimg.cc/XYSrS5LZ/Screenshot-2024-01-17-at-12-21-16-AM.png)




### Tech

This project has made use of the following

- Java 18 : Language of choice. Good multithreading and concurrency controller (lots of boilerplate :(v) )
- Maven : Dependency Management
- Threadpool Executor - For managing lifecycle and running of threads. Creating new thread can be a heavy operation, threadpool executor overcomes this by having a pool of threads which pick up new tasks as it gets free instead of spawning new threads.
- Guava Ratelimiter - A third party rate limiter which is thread safe, i.e, when different threads are scraping simultaeneously through given URLS, this wouldn't run into race conditions
- Other Threadsafe elements - Threadsafe Hashmap, atomic constants etc.
- Docker - So the program can run seemlessly on your systems

### Output

Output of running these over 40k URLs reulted in the below for me
```
{
    "the": 672660,
    "and": 333788,
    "that": 174713,
    "with": 112792,
    "The": 107549,
    "you": 94693,
    "has": 62883,
    "have": 61546,
    "from": 59956,
    "your": 49436
}
```


