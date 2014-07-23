entity-sentiment-bible
======================

Fooling around with Stanford NLP to analyze some biblical texts ;)
This is just a simple introduction to the Stanford NLP package, and what easier way to demonstrate the abilities than reading most famous text of all of mankind? The holy bible.

I do not intend to hurt any feelings with this program, nor is it a mockery of any religions, thoughts or views.

Its just a simple starting point so that people like me, who were new to NLP, can start scratching the surface.

Please feel free to replace the bible text that I am using (obtained from http://printkjv.ifbweb.com/#downloads) with any religious or non religious text of your choosing.

This is a VERY SIMPLE program. I didn't think I should make the new NLP programmer's life too easy, so this program doesn't have actual entity and related sentiment extraction, but instead just shows the sentiment related to a sentence and the entities found in that sentence.
Nor does it have coreference resolution.
If you have questions about more advanced concepts like those (such as finding shortest path between entities and sentiments, coref resolution etc.) please feel free to message me!

At the end of execution, you should hopefully have all the people and locations in the bible, along with the sentiment that is expressed in the sentences that refer to them.
Run instructions:
1. Checkout code  

2. Make sure you have maven installed (verify by typing mvn at the command line)  

3. Type "mvn clean install"  

4. Presuming step 3 completed without any errors, type "mvn exec:java"  

5. Sit back, and enjoy.  

6. Once the program finishes running, look for the file "result.txt" under the project/data directory  

7. Clap your hands, you are now taking the first step in to the land of NLP!  

