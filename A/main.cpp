#include <iostream>
#include "parsing_tree.h"

using namespace std;

string input_rules = R"(
<File>          ::= <Expression>
<Expression>    ::= <Disjunction> | <Disjunction> '->' <Expression>
<Disjunction>   ::= <Conjunction> | <Disjunction> '\|' <Conjunction>
<Conjunction>   ::= <Denial> | <Conjunction> '&' <Denial>
<Denial>        ::= '!' <Denial> | <Variable> | '\(' <Expression> '\)'
<Variable>      ::= '[A-Z][A-Z0-9’]*'
)";

int main() {
    parsing_tree pt;

    cout << (pt.set_input_rules(input_rules) ? "True" : "False") << endl;

//    std::cout << rules << std::endl;
    return 0;
}
