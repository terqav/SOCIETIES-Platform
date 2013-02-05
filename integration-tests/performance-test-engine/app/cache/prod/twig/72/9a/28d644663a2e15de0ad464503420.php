<?php

/* SocietiesTestEngineBundle:Administration:nodes.html.twig */
class __TwigTemplate_729a28d644663a2e15de0ad464503420 extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = $this->env->loadTemplate("SocietiesTestEngineBundle::layout.html.twig");

        $this->blocks = array(
            'title' => array($this, 'block_title'),
            'body' => array($this, 'block_body'),
        );
    }

    protected function doGetParent(array $context)
    {
        return "SocietiesTestEngineBundle::layout.html.twig";
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        $this->parent->display($context, array_merge($this->blocks, $blocks));
    }

    // line 3
    public function block_title($context, array $blocks = array())
    {
        $this->displayParentBlock("title", $context, $blocks);
        echo " - Nodes";
    }

    // line 5
    public function block_body($context, array $blocks = array())
    {
        // line 6
        echo "\t";
        $this->displayParentBlock("body", $context, $blocks);
        echo "
\t
\t
\t<h3>Test Nodes List</h3>
\t<ul>
  \t\t";
        // line 11
        if (isset($context["nodes_list"])) { $_nodes_list_ = $context["nodes_list"]; } else { $_nodes_list_ = null; }
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable($_nodes_list_);
        foreach ($context['_seq'] as $context["_key"] => $context["node"]) {
            // line 12
            echo "   \t\t\t <li><a href=\"";
            if (isset($context["node"])) { $_node_ = $context["node"]; } else { $_node_ = null; }
            echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("admin_get_test_node", array("node_id" => $this->getAttribute($_node_, "nodeId"))), "html", null, true);
            echo "\">";
            if (isset($context["node"])) { $_node_ = $context["node"]; } else { $_node_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_node_, "nodeId"), "html", null, true);
            echo "</a></li>
 \t\t";
        }
        $_parent = $context['_parent'];
        unset($context['_seq'], $context['_iterated'], $context['_key'], $context['node'], $context['_parent'], $context['loop']);
        $context = array_merge($_parent, array_intersect_key($context, $_parent));
        // line 14
        echo "\t</ul>
\t
\t<br/>
\t
\t<h3>Add a New Test Node</h3>
\t
\t\t";
        // line 20
        if (array_key_exists("result", $context)) {
            // line 21
            echo "\t\t\t<p>";
            if (isset($context["result"])) { $_result_ = $context["result"]; } else { $_result_ = null; }
            echo twig_escape_filter($this->env, $_result_, "html", null, true);
            echo "</p>
\t\t";
        }
        // line 23
        echo " \t\t";
        $this->env->loadTemplate("SocietiesTestEngineBundle:Administration:test_node_form.html.twig")->display($context);
        // line 24
        echo "\t
";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle:Administration:nodes.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  72 => 20,  115 => 42,  110 => 40,  96 => 8,  66 => 28,  21 => 1,  53 => 10,  51 => 12,  49 => 13,  331 => 100,  325 => 96,  322 => 95,  318 => 94,  315 => 93,  310 => 90,  304 => 86,  301 => 85,  297 => 84,  294 => 83,  289 => 80,  275 => 79,  271 => 77,  256 => 75,  246 => 73,  243 => 72,  237 => 70,  232 => 69,  224 => 66,  214 => 62,  200 => 61,  197 => 60,  190 => 58,  169 => 56,  163 => 52,  154 => 50,  136 => 49,  132 => 47,  129 => 46,  121 => 42,  113 => 41,  80 => 40,  74 => 21,  59 => 24,  52 => 14,  139 => 45,  124 => 42,  118 => 43,  109 => 34,  99 => 33,  84 => 24,  81 => 23,  73 => 20,  69 => 19,  62 => 16,  41 => 7,  123 => 24,  108 => 20,  95 => 18,  90 => 28,  87 => 5,  83 => 23,  26 => 4,  34 => 5,  102 => 34,  78 => 23,  61 => 13,  56 => 12,  38 => 6,  92 => 27,  86 => 24,  46 => 11,  37 => 6,  33 => 5,  29 => 7,  19 => 1,  44 => 12,  27 => 3,  55 => 23,  48 => 7,  45 => 10,  36 => 6,  30 => 4,  25 => 3,  248 => 96,  238 => 90,  234 => 88,  227 => 84,  223 => 83,  218 => 63,  216 => 79,  213 => 78,  210 => 77,  207 => 76,  198 => 71,  192 => 67,  177 => 61,  174 => 60,  171 => 59,  164 => 55,  160 => 51,  155 => 51,  153 => 50,  149 => 47,  146 => 47,  143 => 46,  137 => 45,  126 => 43,  116 => 22,  112 => 37,  107 => 31,  85 => 28,  82 => 45,  77 => 39,  67 => 17,  63 => 14,  32 => 7,  24 => 6,  22 => 4,  23 => 3,  20 => 2,  17 => 1,  201 => 72,  195 => 66,  187 => 62,  181 => 63,  178 => 57,  172 => 57,  168 => 54,  165 => 53,  156 => 51,  151 => 45,  148 => 44,  145 => 43,  142 => 42,  134 => 44,  131 => 44,  128 => 35,  122 => 32,  119 => 31,  111 => 21,  106 => 29,  103 => 28,  100 => 27,  97 => 28,  93 => 7,  89 => 16,  79 => 12,  68 => 30,  64 => 14,  60 => 22,  57 => 15,  54 => 12,  50 => 9,  47 => 10,  43 => 9,  39 => 9,  35 => 5,  31 => 4,  28 => 3,);
    }
}
