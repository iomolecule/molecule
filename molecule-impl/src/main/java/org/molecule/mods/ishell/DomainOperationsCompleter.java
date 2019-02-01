package org.molecule.mods.ishell;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.molecule.system.services.DomainService;

import java.util.List;
import java.util.Stack;

class DomainOperationsCompleter implements Completer{

    private DomainService domainService;
    private Stack<String> domainStack;

    DomainOperationsCompleter(DomainService domainService,Stack<String> domainStack){
        assert domainService != null;
        assert domainStack != null;
        this.domainService = domainService;
        this.domainStack = domainStack;
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> candidates) {
        assert candidates != null;

        String fullyQualifiedDomain = JLineInteractiveShell.getFullyQualifiedDomain(domainStack);

        List<String> operationsAt = domainService.getOperationsAt(fullyQualifiedDomain);

        if(operationsAt != null && !operationsAt.isEmpty()){


            for (String opName : operationsAt) {
                Candidate candidate = new Candidate(opName);
                candidates.add(candidate);
            }

        }

    }
}
