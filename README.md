# RedditCrawler
A reddit crawler starting at /r/dankmemes, grabs related subreddits from the sidebar, and then visits all of those subreddits and grabs their related subreddits and repeats until there is nowhere left to go. Then, I trim out any subreddits with less than 1k subscribers. This leaves some subreddits stranded, with no links to them from the central cluster, and as such they form a sort of reddit "oort cloud". I run an algorithm called OpenOrd to form the clusters. Every cluster is assigned a color. Modularity analysis says there are 377 clusters. Node size is determined by number of subscribers.

Then I ran an expansion algorithm to spread out the densly packed clusters to make it easier to see what is going on

To make things easier to read, only subreddits with more than 10k subscribers get a label

Simply import nodes.csv and then edges.csv into Gephi as a spreadsheet and play around with the various visualizations. You might need a beefy computer to run some of them. Or use the Data Laboratory to filter it down a bit.

# Results

http://imgur.com/a/pfn6H
