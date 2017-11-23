# InformationExtraction


(a) CoreNLP - https://stanfordnlp.github.io/CoreNLP/download.html
(b) Cade Machine 15 took about 3 minutes to process a single (25 Line) document
(c)
    Domas Piragas 
        Created Template class used for aggregating template data, and formatting the output properly.
        Boilerplate code used to parse input file breaking it apart into individual stories for processing
        and writing to the proper output file in the proper format. Set up skeleton for project so that
        the logic for extracting each category is decoupled and easily plugged into the final output. 
        Extracted the Document ID and Incident information for each template. Created shell script and
        set up project to compile/run independent from any IDE or implemented libraries. 
        
    Maryam Barouti
        For the part weapon, I extracted some key words for this category and checked if we have any words
        like that in the text and used the nounPhrase method to extract the whole name of weapon. 
        For target I did the same as weapon and since the target is mostly a place I extracted the whole possible places
        and if there was such keyword in the text, I returned the noun phrase related to that
        For Target, I used dependency parsing and of I had for example murdered and murder of or attack againts it will 
        return the noun phrase after it. 
        Implemented a chunking method for returning noun phrases to have complete answer for each category.
        Implemented the Target finder, victim finder and weapon finder as follows:
        
        
(d) PerpIndiv and PerpOrg are currently not being extracted. 
    Victims and Targets will occasionally have duplicates. 