RHDM Template
============================================

Example of how to use rule templates in Red Hat Decision Manager v7.x (Drools).

- **rule-template** shows how to build a rule base extracting the template parameters from a DB.
  Worth noticing that the maven plugin generate the DRL out of the DB and then generate the executable model
- **rule-consumer** shows how to consume the kjar generated from rule-template

To run the example:

1. Generate the kjar

    ```sh
    cd rule-template
    mvn clean install
    ```

2. Launch the rule consumer logic