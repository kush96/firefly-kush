# Top Words Fetcher
##### Kush Singh Assignment Submission
My submisssion for the assignment [here](https://docs.google.com/document/d/1GdLjenD201pNl3LOmvaBM0a56DIIEC88fBd3kimBPrQ/edit?pli=1).
### Installation Guide

- Type some Markdown on the left
- See HTML in the right
- ✨Magic ✨

### Tech

This project has made use of the following

- Java 18 : Language of choice. Good multithreading and concurrency controller (lots of boilerplate :(v) )
- Maven : Dependency Management
- Threadpool Executor - For managing lifecycle and running of threads. Creating new thread can be a heavy operation, threadpool executor overcomes this by having a pool of threads which pick up new tasks as it gets free instead of spawning new threads.
- Guava Ratelimiter - A third party rate limiter which is thread safe, i.e, when different threads are scraping simultaeneously through given URLS, this wouldn't run into race conditions
- Other Threadsafe elements - Threadsafe Hashmap, atomic constants etc.
- Docker - So the program can run seemlessly on your systems

### Workflow Diagram

![Workflow Diagram](https://i.postimg.cc/XYSrS5LZ/Screenshot-2024-01-17-at-12-21-16-AM.png)




