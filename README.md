# InformationExtraction


(a) OpenNLP - https://opennlp.apache.org/
(b) Cade Machine 15 took about 15 seconds to process a single (25 Line) document
(c)

    Domas Piragas 
        Created Template class used for aggregating template data, and formatting the output properly.
        Boilerplate code used to parse input file breaking it apart into individual stories for processing
        and writing to the proper output file in the proper format. Set up skeleton for project so that
        the logic for extracting each category is decoupled and easily plugged into the final output. 
        Extracted the Document ID and Incident information for each template. Created shell script and
        set up project to compile/run independent from any IDE or implemented libraries. Implemented coreference
        resolution, which was not taken advantage of due to poor performance. Resolved bugs which were causing
        large slowdowns in performance. Code cleanup, reducing size of files by half. 
        
    Maryam Barouti
        -extracted patterns in the patterns.java to use for event extractions for target, victim, perpetrator organization and perpetrator individual
        -defined keywords to distinguish between perpetrator organization and perpetrator individual in Constants.java
        -defined keywords to distinguish between target and victim in Constants.java
        -implemented methods to find the noun phrase from the word extracted by patterns
        -implemented methods to remove unwanted common characters or words
        -implemented methods to remove duplications and find the results which have AND in between
        
        
(d) N/A 